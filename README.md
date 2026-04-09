# Language Interpreter

This project is an interpreter for a small mock language. It is built as a pipeline that takes source code, turns it into tokens, builds an AST, and then evaluates that AST into the final program state.

## Execution pipeline

The interpreter follows four stages:

1. **Language** — the original source text written in the mock language.
2. **Tokens** — `Tokenizer` converts the source into a stream of tokens.
3. **AST** — `Parser` turns the tokens into an abstract syntax tree.
4. **Eval** — `Evaluator` walks the AST and produces the final variable map.

In short:

```text
language -> tokens -> AST -> eval
```

This separation keeps the code easier to understand and makes each stage independently testable.

## Testing

Each stage of the pipeline is tested in isolation:

- `TokenizerTest` checks tokenization.
- `ParserTest` checks AST construction.
- `EvaluatorTest` checks execution of the parsed tree.
- `GivenExamplesTest` is based on the example programs provided in [`language-example.txt`](language-example.txt). It acts as a higher-level integration check for the language as a whole.

## Run tests

Run the test suite with:

```bash
mvn test
```

