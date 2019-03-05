Datagenio is a tool for building web application models, based on their HTTP requests set. 
It is also a tool to generate data sets based on those requests, with tests data. 
It can explore any AJAX-based web application. 

As output, it generates a graph model, stored in an embedded Neo4J database.
It requires the Firefox browser to run.  

### Usage

To run the application, use command:

```java -jar datagenio-{version}.jar -url {url} -o {output-dir}```

A more complete configuration can be used, by passing it to the command with option `-C`. 

```java -jar datagenio-{version}.jar -C {path-to-config-file}```

A sample configuration file is shown below.

```json
{
	"crawl_depth": 25,
	"transition_weight": 1,
	"url": "https://test.com/",
	"output_directory": "/my/output/dir",
	"output_format": "csv",
	"save_screen_shots": true,
	"save_hars": false,
	"model_only": false,
	"data_set_only": false,
	"continue_model": false,
	"database_configuration": {
		"connection_mode": "embedded",
		"request_save_mode": "json"
	},
	"event_inputs": [
		{
		    "event_id": "my-event-id",
		    "xpath": "/html/body/div[1]/div[2]/form/input[2]",
		    "input_value": "my-password",
		    "input_type": "password"
		}
	],
	"transition_weights": [
		{
			"id": "my-transition-id",
			"weight": 3
		}
	]
}
``` 

To run the tests, use command:

```mvn clean test```

