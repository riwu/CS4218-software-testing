# CS4218 Milestone 2 Report

## TDD Process
1. We adopt slightly different TDD approaches depending on the resources we have:
    **a. Existing interfaces.** Since the template code gave us default interfaces to work on, we can write the failing test cases on those interfaces;
    **b. Breaking feature into subcomponents,** and then write failing test cases for those sub components;
    **c. Comparing other team's test cases**, incorporate missing test cases from other teams test suite into ours.
2. Run and make sure the test cases fail.
3. Write implementation for the said features.
6. Rerun tests until it passes the test cases.
7. Repeat step 1.

We are also adding more unit tests for current milestone implementation.

The experience we had using test cases from other teams show that there is room for differences in design of test cases by checking for exception messages apart from expecting an exception to be thrown.

## Integration Testing
### Pairwise Combinatorial Testing

Due to the sheer amount of combinations, we will use pairwise testing in our project to reduce the number of test cases while achieve reasonable coverage.

One example of pairwise testing is using `paste` and `sed`.

```java
	@Test
	public void shouldReplaceTabsWithSpaceWhenBothFilesMerge() throws Exception {
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s/\\t/ /";
		String expected = REPLACED_TABS;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
	}
```

In the above test case, we use 2 files which are combined using paste to produce text and then replace the tab characters with space using the regex replacement rules of sed.

### Catalogue Testing for positive and negative cases

It's relatively hard to come up with boundary cases by ourselves, so we look for publicly available catalogue instead. The test cases are cherry-picked to suit our needs:

1. Numeric Input Catalogue - https://stackoverflow.com/questions/3153782/test-cases-for-numeric-input
2. Naughty String Catalogue - https://github.com/minimaxir/big-list-of-naughty-strings

We also apply catalogue testing on the same test suite (`paste` and `sed`)

```java
	@Test(expected=SedException.class)
	public void shouldThrowExceptionWhenMissingS() throws Exception {	
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed /\\t/ /";
		shell.parseAndEvaluate(cmdline, System.out);
	}
```
The above is an example of a negative test where the pattern rule is not adhered to.

```java
	@Test
	public void shouldNotReplaceTabsWithSpaceWhenIndexOutOfBoundsStdin() throws Exception {
		String cmdline = "paste " + fileA.toString() + " " + fileB.toString() + "|" + "sed s/\\t/ /50";
		String expected = ORIGINAL_FULL;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdline, baos);
        assertEquals(expected, baos.toString());
	}
```

The above is a corner case where the index is larger than can be found from the text, resulting in no replacement of text.


## Tools
### [Microsoft PICT](https://github.com/microsoft/pict)
 - to generate pairwise test cases automatically

PICT tool allows us to generate pairwise testcase easily according to our specs. However, it does not generate the actual code, but only show us a table what should be tested. The following is a sample PICT script for `semicolon`:

```bash
# criterion
Semicolon:	one, many
Inside Backquote: yes, no, both
suffix: explicit, implicit

# constraints
IF [Semicolon] = "one"	THEN [Inside Backquote] <> "both";
```

The result output will be as follwing:

|id|Semicolon	|Inside Backquote	|suffix|
|---|-----|-----|-----|
|1|many	|no|	explicit|
|2|one	|yes	|implicit|
|3|many	|both	|explicit|
|4|many	|no|	implicit|
|5|one	|no	|explicit|
|6|many	|both|	implicit|
|7|many	|yes	|explicit|

### Travis CI

Travis CI runs our test cases automatically after pushing to remote and before merging a pull request. This allows us to identify regression when code is modified before the master branch is updated.


### Piping & Redirection
Some of the application may or may not read/write to the input/output stream. Therefore, we categorize the application into following:

| App | read from InputStream | write to OutputStream |
|-----|-----------------------|-----------------------|
| echo| no| yes|
| ls| no |  yes |
| cat | yes | yes |
| exit | no | no |
| mkdir| no | no |
| grep | yes | yes |
| paste| yes | yes |
| diff | yes | yes |
| cd | no | no |
| sed | yes | yes |
| split | no | no |
| cmp | yes | yes |

Application such as `split`, `mkdir`, `exit` and `cd` does not read from input or output stream, therefore they are excluded from our pairwise integration test cases.

Application that only write to output stream must be tested first in the pipeline.