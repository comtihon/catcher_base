export interface Validator {
  /**
   * Should return error object in format ace understands or null
   * if data is valid
   * @param data to validate
   */
  validate(data: String): object
}
