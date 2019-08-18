import {StepsAutoCompleter} from "./steps.completer";

describe('StepsAutoCompleter', () => {

  let lines = ["---",
    "  variables:",
    "- test",
    "- other includes",
    "",
    "steps"];
  let completer = new StepsAutoCompleter();

  it('matchCompleteFromStart', () => {
    expect(completer.matchCompleteFromStart(lines, "steps"))
      .toEqual(true);

    expect(completer.matchCompleteFromStart(lines, "includes"))
      .toEqual(false);
  });

  it('getFirstStepComplete', () => {
    expect(completer.getFirstStepComplete(lines, {'row': 4}))
      .toEqual(["includes"]);

    expect(completer.getFirstStepComplete(lines, {'row': 1}))
      .toEqual([]);
  });
});
