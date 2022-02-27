## How to run:
The project uses scala, hence you will need sbt to run it:
After installing sbt do `sbt run` at the root of the project directory.

Or, run the already built jar with

`java -jar swm-assembly-0.1.0-SNAPSHOT.jar`

To exit press `Ctrl+C`

## Important points
An actor[_com.swym.test.mock.MockTwitter_] provides a mock twitter server which sends a tweet every 3 seconds from a list of 9 tweets.

Rules are defined in a file called `rules.json` in the `resources` directory.
Every rule has a data type ( tweet, fbpost etc.) on which it is defined with a condition to check and an optional aggregation step.
A set of actions can be defined as part of each rule which are triggered when the condition of the rule is satisfied.

There are 3 rules defined here:
- First rules calls a 3rd party api if location is either delhi or bangalore
- Second rule also calls 3rd party api if the text of the tweet contains the words `social` or `media`
- Third rule adds an emoji to every 5th tweet by a user


## Approach

Usually problems of this kind are solved by creating a DSL which is then used to
apply conditional logic on the properties of a data object. It becomes progressively difficult to maintain and enhance the capabilities of such an ad hoc language as requirements change.

So instead of writing a new dsl for describing rules, here I have used SQL.
This allows us to immediately use a large part of the sql language to define rules without writing any code.
To interpret and execute the sql I have used an in memory db called h2.

When a tweet comes it is stored as  row in the db. Then rules are executed as sql queries to find the matching ones.
For rules which requires some kind of aggregation across events( tweets), the said aggregations are stored in the db as well.
Queries are then executed on those aggregate tables.

This scheme requires the rule creator to be aware of the table schema which will be used to execute the rule.
This is not much different from learning a custom dsl. On the other hand it's a simple mental model where the data becomes a table against which you are trying to run queries and see which queries match a record.

## Limitations
- Various runtime failure modes are not considered in this implementation
- Aggregations need to be persisted for use across restarts of the app. For this a cache like redis can be used.
- Every kind of aggregation requires a new table
- No unit tests added

## Enhancements
- A production applications will want to separate rules evaluation and trigger of actions by using Kafka like queues.
- Error handling and alerting
- UI based rules builder with capability to validate the query instantly
- Rules needs to stored in a db for live editing capability

## Possible future capabilities
- Aggregations can be reused to create matrices( dashboards etc.) e.g. count of tweets for locations of interest
- Actions can lead to creation of new Content which then can be fed back to the system to trigger new rules. ex: an action that fetches new content from an api



