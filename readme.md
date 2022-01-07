# Prometheus/Grafana Example

## Usage:

Run
```
docker-compose up --remove-orphans --build --force-recreate 
```

And browse to
* [localhost:8080](http://localhost:8080) will show some basic usage text on how to mess w/ metrics
* [localhost:8090](http://localhost:8090) to see the metrics which will be scraped
* [localhost:9090/graph](http://localhost:9090/graph) to mess with [prometheus](https://prometheus.io/)
* [localhost:3000](http://localhost:3000) to create some [grafana](https://grafana.com/) dashboards

See [App.scala](App.scala) for the REST code which uses [dropwizard](https://www.dropwizard.io/en/latest/) to drive the metrics

## What is this project? 
This project was created to demonstrate:
 * Using dropwizard counters, meters and histograms
 * Exposing those metric MBeans via an HTTP endpoint using jmx_prometheus and config.yaml
 * Putting it all together with Prometheus and Grafana in a docker-compose

The end result of running `docker-compose up` will be that:

#### You can easily create/update counters, meters and histograms via GET requests in your browser

 * [localhost:8080/inc/foo](http://localhost:8080/inc/foo) will increment the 'foo' counter (for any string 'foo')
 * [localhost:8080/dec/foo](http://localhost:8080/dec/bar) will decrement the 'bar' counter (for any string 'bar')
 * [localhost:8080/meter/fizz](http://localhost:8080/meter/fizz) will mark the 'fizz' meter (for any string 'fizz')
 * [localhost:8080/histogram/buzz/123](http://localhost:8080/histogram/buzz/123) will set the value 123 of the 'buzz' histogram (for any string 'buzz')
 * [localhost:8080](http://localhost:8080) will show some basic usage text

#### You see the JMX metrix
Either use `jconsole` to browse the app_lication MBeans.
The names of which are matched in the `config.yaml` in order to expose nice key/value pairs.

Browse to [localhost:8090/metres](http://localhost:8090/metres) to see the exposed key/value pairs

# Why was it created?

There can be a lot of moving parts in application telemetry, and on most projects that all gets a bit muddled together/confusing.

This project allowed me to build up/play with each element independently and understand the what/why/how of each:

 * metrics gathering (counters, meters, gauges) (this project uses [dropwizard](https://www.dropwizard.io/en/latest/))
 * Those metrics typically get exposed on the JVM using [MBeans](https://docs.oracle.com/javase/tutorial/jmx/mbeans/index.html) which you can see using [jconsole](https://openjdk.java.net/tools/svc/jconsole/).
   That's not ideal - typically external applications such as [new relic](https://newrelic.com/) or [prometheus](https://prometheus.io/) (this example uses prometheus) can more easily just scrape an HTTP endpoint.
 * To expose MBeans as an http endpoint, you use an agent such as [prometheus jmx_exporter](https://github.com/prometheus/jmx_exporter) (which entails using its config.yaml)

Phew! That's all just to set up our app. It's great to then be able to spin up a local [prometheus](https://prometheus.io) and [grafana](https://grafana.com/) to play with.

This project is meant to just be an example of all of those pieces working together.

## What's Cool

I love [scala](https://scala-cli.virtuslab.org/) - it remains the most scalable, flexible, powerful language I've found, and it has some amazing tooling.
Once you learn it, you find other languages lacking in one way or another.

Scala can be daunting/complicated, but it can also be amazingly simple. 

In this case, I used Lihaoyi's [cask](https://github.com/com-lihaoyi/cask) library and VirtusLab's [scala-cli](https://scala-cli.virtuslab.org/) to package a 
very simple REST [App](App.scala).

Put them both together, and you have a single scala file in less than 100 lines of code which, incidentally, is also buildable by declaring its own dependencies at the top:
```
// using scala 3.1.0
// using lib com.lihaoyi::cask:0.8.0
// using lib io.dropwizard.metrics:metrics-core:4.1.2
// using lib io.dropwizard.metrics:metrics-jmx:4.1.2
```

how super-cool is that!?!?

## Gotchas/Lessons learned
When using [cask](https://github.com/com-lihaoyi/cask), you have to:
```
override def host: String = "0.0.0.0"
```
Otherwise you won't be able to browse to [http://localhost:8080](http://localhost:8080) from inside Docker. 

# Development

You should just need docker to:
```
docker-compose up --remove-orphans --build --force-recreate 
```

This basic app uses [cask](https://com-lihaoyi.github.io/cask/) and [dropwizard](https://metrics.dropwizard.io/4.1.2/getting-started.html)
to allow users to easily mess with metrics, exported using [Prometheus JMX Exporter](https://github.com/prometheus/jmx_exporter)

## Running

Essentially if you have [scala-cli](https://scala-cli.virtuslab.org/docs) installed, just: 
```
scala-cli App.scala
```

or, using the convenience build.sh:
```
source build.sh && run
```

## Browser Usage
And use GET verbs (naughty) in your browser to e.g.
 * increment 'foo' (just any name will do):  [http://localhost:8080/inc/foo](http://localhost:8080/inc/foo)
 * decrement 'bar' (just any name will do):  [http://localhost:8080/dec/bar](http://localhost:8080/dec/bar)
 * set value 123 on histogram 'whatevs':  [http://localhost:8080/histogram/whatevs/123](http://localhost:8080/histogram/whatevs/123)
 * set mark on meter 'dave':  [http://localhost:8080/meter/dave](http://localhost:8080/meter/dave)
 