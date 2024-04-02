## sbt project compiled with Scala 3

## TODO

* parsing json in http server
* error handling - not found responses 
* get all command (scan command or keys?)
* unit-tests
* redis persistance between shutdown 
* split routes into domain entities - for each we should have create, update, delete, list/show all, and get

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).
