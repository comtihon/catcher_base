import * as yaml from "js-yaml";
import {Validator} from "./validator";

export class AceYamlValidator implements Validator {

  public validate(data: String) {
    try {
      // TODO custom validator (only allowed steps properties)
      yaml.safeLoad(data);
      return null;
    } catch (e) {
      return {
        row: e.mark ? e.mark.line - 1 : 0,
        column: e.mark ? e.mark.position - 1 : 0,
        text: e.reason,
        type: "error"
      }
    }
  }
}
