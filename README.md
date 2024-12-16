## Attempt at the Jepsen challenge with Java

Distributed Systems Challenge: [Jepsen](https://github.com/jepsen-io/maelstrom/blob/main/doc/workloads.md)

### Prerequisites
1. [Maelstrom](https://github.com/jepsen-io/maelstrom/blob/main/doc/01-getting-ready/index.md)
2. Java 21+
3. [Gradle](https://gradle.org/)
4. [GraalVM](https://www.graalvm.org/)

### Setup
1. Download [Maelstrom](https://github.com/jepsen-io/maelstrom/blob/main/doc/01-getting-ready/index.md)
2. Download [GraalVM](https://www.graalvm.org/)
3. Download [Gradle](https://gradle.org/)
4. Clone this repository
5. Uncomment the requisite challenge in Runner.java (/src/main/java/io/jespen/Runner.java)
6. Run `gradle build && gradle nativeCompile`

### Run
1. Run `{path-to-maelstrom}/maelstrom test -w echo --bin ./build/native/nativeCompile/jespen-runner --node-count 2`

### Notes
1. This is a work in progress.

