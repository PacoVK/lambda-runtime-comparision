const {
  SFNClient,
  StartExecutionCommand,
  ListStateMachinesCommand,
  DescribeExecutionCommand,
  ExecutionStatus,
} = require("@aws-sdk/client-sfn");
const {
  LambdaClient,
  ListFunctionsCommand,
} = require("@aws-sdk/client-lambda");
const powerTuningInput = require("./powerTuningInput.json");

const javaBasedPowerValues = [512, 1024, 1536, 3008];

const groovyPowerValues = [1024, 1536, 3008];

const sfnClient = new SFNClient({});
const lambdaClient = new LambdaClient({});

const getBenchmarkTarget = async () => {
  if (process.argv.length !== 3) {
    throw Error(`Expected one argument, received ${process.argv.length - 2}`);
  }
  const targetLambdaName = process.argv[2];
  console.log("Searching for Lambda with name", targetLambdaName);

  const command = new ListFunctionsCommand({});
  const response = await lambdaClient.send(command);
  const targetLambda = response.Functions.find((lambda) =>
    lambda.FunctionName.includes(targetLambdaName)
  );
  if (!targetLambda) {
    throw Error(`Could not find any Lambda that matches ${targetLambdaName}`);
  }
  return targetLambda;
};

const getStatemachine = async () => {
  const listStateMachinesCommand = new ListStateMachinesCommand({});
  const data = await sfnClient.send(listStateMachinesCommand);
  const { stateMachineArn } = data.stateMachines.find((statemachine) =>
    statemachine.name.startsWith("powerTuningStateMachine")
  );
  return stateMachineArn;
};

const setAndSanitizeBenchmarkParamsForJava = (targetLambda) => {
  const benchmarkInput = powerTuningInput;
  benchmarkInput.lambdaARN = targetLambda.FunctionArn;
  if (targetLambda.Runtime?.startsWith("java")) {
    benchmarkInput.powerValues = javaBasedPowerValues;
    if (targetLambda.FunctionName.includes("groovy")) {
      benchmarkInput.powerValues = groovyPowerValues;
    }
  }
  return benchmarkInput;
};

const runBenchmark = async (stateMachineArn, input) => {
  const params = {
    stateMachineArn: stateMachineArn,
    input: JSON.stringify(input),
  };
  const command = new StartExecutionCommand(params);
  console.log(`Starting Lambda benchmark`);
  const data = await sfnClient.send(command);
  return data.executionArn;
};

const sleep = (ms) => {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
};

const waitForSuccess = async (executionArn) => {
  console.log("waiting for benchmark completion");
  const command = new DescribeExecutionCommand({
    executionArn: executionArn,
  });
  const response = await sfnClient.send(command);
  if (response.status === ExecutionStatus.RUNNING) {
    console.log("Benchmark is still running...");
    await sleep(5000);
    await waitForSuccess(executionArn);
  } else if (response.status !== ExecutionStatus.SUCCEEDED) {
    throw Error(
      `Benchmark failed with status ${response.status}! Execution: ${executionArn}`
    );
  } else {
    console.log("Benchmark successfully");
    console.log(response.output);
  }
};

const triggerPowerTools = async () => {
  const stateMachineArn = await getStatemachine();
  console.log("Using StateMachine: ", stateMachineArn);
  const targetLambda = await getBenchmarkTarget();
  console.log("Found Lambda ", targetLambda.FunctionName);
  const benchmarkInput = setAndSanitizeBenchmarkParamsForJava(targetLambda);
  const executionArn = await runBenchmark(stateMachineArn, benchmarkInput);
  await waitForSuccess(executionArn);
};

triggerPowerTools();
