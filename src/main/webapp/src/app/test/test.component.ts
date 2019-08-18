import {Component, OnInit, ViewChild} from '@angular/core';
import {Project} from "../shared/model/project";
import 'brace'
import 'brace/mode/yaml'
import 'brace/mode/json'
import 'brace/ext/language_tools'
import {StepsAutoCompleter} from "./completer/steps.completer";
import {Validator} from "./validator/validator";
import {AceYamlValidator} from "./validator/yaml.validator";
import * as yaml from "js-yaml";
import {AlertService} from "../shared/services/alert.service";
import {TestService} from "../shared/services/test.service";
import {Test} from "../shared/model/test";

@Component({
  selector: 'app-new-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent implements OnInit {

  @ViewChild('editor', {static: true}) editor;
  @ViewChild('yaml', {static: true}) yaml;
  @ViewChild('json', {static: true}) json;
  @ViewChild('testName', {static: true}) testName;
  options: any = {
    enableBasicAutocompletion: true,
    enableLiveAutocompletion: true,
    maxLines: Infinity,
    fontSize: "100%"
  };
  content: string;
  mode = 'yaml';
  project: Project;
  validator: Validator = null; // TODO change me when mode changes
  model = {
    yaml: true,
    json: false
  };

  constructor(private alertService: AlertService, private testService: TestService) {
  }

  ngOnInit() {
    this.project = history.state;
    this.content = '';
    this.editor.getEditor().completers = [new StepsAutoCompleter()];
    this.validator = new AceYamlValidator();
  }

  onChange(event) {
    this.alertService.clear();
    if (this.validator) {
      let error = this.validator.validate(event);
      if (error)
        this.editor.getEditor().getSession().setAnnotations([error]);
      else
        this.editor.getEditor().getSession().setAnnotations([]);
    }
  }

  onSave() {
    // TODO require test name?
    this.testService.newTest(this.project, new Test(this.testName.text, this.content));
    //  TODO handle response
  }

  /**
   * Switch input mode if syntax is valid, including validators.
   * @param mode json/yaml
   */
  onClick(mode: String) {
    if (this.content.length > 0)
      if (!this.convertText()) { // can't convert text with errors, revert buttons back
        this.alertService.error("Can't switch with syntax error.");
        console.log(this.model);
        if (this.model.yaml == true) {
          // from json to yaml
          this.model.yaml = true;
          this.model.json = false;
          this.yaml.nativeElement.checked = true;
          this.json.nativeElement.checked = false;
        } else if (this.model.json == true) {
          this.model.yaml = false;
          this.model.json = true;
          this.yaml.nativeElement.checked = false;
          this.json.nativeElement.checked = true;
        }
        return;
      }
    if (this.model.yaml == true && (mode == 'yaml' || mode == 'json')) {
      // yaml deselected -> switch to json or just json selected
      this.model.yaml = false;
      this.model.json = true;
      this.validator = null;
      this.mode = "json";
    } else if (this.model.json == true && (mode == 'json' || mode == 'yaml')) {
      // json deselected -> switch to yaml
      // or just yaml selected
      this.model.yaml = true;
      this.model.json = false;
      this.mode = "yaml";
      this.validator = new AceYamlValidator();
    }
  }

  private convertText(): boolean {
    try {
      if (this.mode == "json") {
        this.content = yaml.dump(JSON.parse(this.content));
      } else {
        this.content = JSON.stringify(yaml.safeLoad(this.content));
      }
      if (!this.content)  // yaml.dump can return undefined on empty document
        this.content = "";
      return true;
    } catch (_) {
      return false;
    }
  }

}
