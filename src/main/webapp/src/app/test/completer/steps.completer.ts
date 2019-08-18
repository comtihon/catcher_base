import * as yaml from "js-yaml";

export class StepsAutoCompleter {

  getCompletions(editor, session, pos, prefix, callback) {
    // TODO get text from editor
    // TODO find current object by the position
    // TODO get autocomplete list for current object (count already inserted fields)
    let autocomplete = this.getFirstStepComplete(editor.session.doc.$lines, pos);
    // TODO in case of first step autocomplete is empty run second step autocomplete
    //let text = editor.session.doc.$lines.join('\n');
    // try {
    //   let parsed = yaml.safeLoad(text);
    //   console.log("parsed " + parsed);
    //   if (!parsed || pos.column == 1) {
    //   }
    // } catch (e) {
    //   console.log("parsing error")
    // }
    callback(null, autocomplete.map(function (word) {
      return {
        caption: word,
        value: word,
        meta: "static"
      };
    }));
  }

  getFirstStepComplete(lines: Array<String>, pos): Array<String> {
    let firstStepCompletes = ["variables", "steps", "includes"];
    if (lines[pos.row].trim().length == 0) { // empty line with no keys
      return firstStepCompletes.filter(complete => !this.matchCompleteFromStart(lines, complete))
    }
    return []
  }

  matchCompleteFromStart(lines: Array<String>, complete: string): boolean {
    for (let line of lines) {
      if (line.trim().startsWith(complete))
        return true
    }
    return false
  }
}
