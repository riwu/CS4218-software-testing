# CS4218 Hackathon Report

## General method used in testing

- We imported our set of test cases and the given test cases in `Lab and Project/MS2_TDD_Testcases.zip` to the project and run the tests then verify that the failed test cases are not due to differences in formatting or implementation
- Do system testing to check if the applications handle a change of directory correctly
- Do system testing to check if the applications handle relative and absolute paths correctly
- Do black box testing

## Bug Report Table

| Bug Report Number | Description | Test Case | Comments |
| -- | -- | ---------------------- | -- |
| 1 | mkdir after cd is not creating in current directory | `cd ..; mkdir newdir` |  |
| 2 | ls using an absolute path on Windows appends the path to current working directory | `ls C:\` | |
| 3 | ls with * throw access error for hidden files and ls hidden directories when should be ignored| `ls *` | |
| 4 | ls fail to glob when relative path present | `ls -d ../*` ||
| 5 | ls empty directory prints directory name| `ls emptydir`||
| 6 | cat fails immediately if any arg is invalid instead of printing until invalid arg | `cat pom.xml folder`
| 7 | cat with empty string goes into infinite loop | `cat ""`||
| 8 | cmp for files does not seem to show difference | `cmp thisistest.txt thisistext.txt` ||
| 9 | cmp for files does not seem to show difference for files with only differ blank lines | `cmp thisisblanktext.txt thisistext.txt` ||
| 10 | cmp for files with s option does not seem to show difference | `cmp -s thisistest.txt thisistext.txt` ||
| 11 | cmp for files with l option does not seem to show difference | `cmp -l diffdir1/testdiff.txt diffdir2/testdiff.txt` ||
| 12 | cmp for files with c option does not seem to show difference | `cmp -c thisistest.txt thisistext.txt` ||
| 13 | cmp for files with c and l option does not seem to show difference | `cmp -lc diffdir1/testdiff.txt diffdir2/testdiff.txt` ||
| 14 | cmp when stdin is first argument throws an error message | `cat thisistest.txt | cmp - thisistext.txt` | |
| 15 | cmp does not seem to show difference when reading from stdin | `cat thisistest.txt | cmp thisistext.txt -` ||
| 16 | cmp does not resolve absolute path properly on Windows | `cmp <absolute path to project directory>\thisistext.txt thisistext.txt` | |
| 17 | diff on an absolute path on Windows throws unknown error |`diff <absolute path to project directory>\thisistext.txt thisistext.txt`| |
| 18 | diff binary files throws error| `diff diffdir1/example.jpg diffdir2/example.jpg`|
| 19 | diff different files does not group different lines from same file together and is not mentioned in assumption| `diff diffdir1/testdiff.txt diffdir2/testdiff.txt` |
| 20 | diff different files does not print diff from first file first but line that comes earlier first| `diff diffdir2/testdiff.txt diffdir1/testdiff.txt` |
| 21 | diff with STDIN is always printed second, but not mentioned | `cat thisistext.txt | diff - thisistext.txt` | |
| 22 | Misleading error (unknown error) when diff on non-existent files/directories. | `diff test1 test2` | |
| 23 | diff on directories without option does not show if there's difference in files/binary files with same name| `diff diffdir1 diffdir2` | |
| 24 | diff on directories without option does not show if there's a common sub directory| `diff diffdir1 diffdir2` | |
| 25 | diff on directories with q option does not show if there's difference in files/binary files with same name| `diff -q diffdir1 diffdir2` | |
| 26 | diff on directories with s option does not show when there are files that are identical | `diff -s diffdir1 diffdir2` | |
| 27 | diff on directories with sB option does not show when there are files with blank lines are identical | `diff -sB diffdir1 diffdir2` | |
| 28 | Sed: Assumptions state index must be positive, non-zero but replaces first instance when a negative number is used and did not throw error.| `sed s/This/That/-1 thisistext.txt` | |
| 29 | Sed unable to handle other symbol as separator| `sed "s|This|That|" thisistext.txt`|
| 30 | Sed throws an error if pattern contains leading `*` | `sed s/*/a/ pom.xml`|
| 31 | split prints several blank lines on console on success | `split -l 1 pom.xml c` |
| 32 | split does not work if prefix is omitted (should default to `z`) | `split -l 1 pom.xml` |
| 33 | split does not work if file is passed through stdin | `split -l 1 < pom.xml` | |
| 34 | split does not work if options are omitted (should default to `-l 1000`) | `split pom.xml c` |
| 35 | split does not throw error if line option is 0 | `split -l 0 pom.xml a `
| 36 | split does not throw error if byte option is 0 | `split -b 0 pom.xml a ` 
| 37 | split does not work for large numbers | `split -l 2234567890 pom.xml a`
| 38 | Split prefix will not follow topological order. From comments in SplitFilenameGenerator: An example sequence xaa..xzz..xzaaa..xzzzz... where xzaaa is actually higher oder than xzz when sorted in topological order.![](https://i.imgur.com/hlBiu0o.png) | `cat x* > combinedfile` will fail| 
| 39 | Assumption on ability to split any number of files is false because it eventually terminated with "split: /tmp/xzfap (Too many open files)" | `split -b 1 bin.out x` |
| 40 | split by bytes adds extra bytes to the last file written if <size of file> % `bytesPerFile` != 0 (eg. bytesPerFile == 2 && size of file is an odd number) |  `split -b 3 folder/a a`
| 41 | using - to read stdin for paste throws error of no input stream | `grep test thisistext.txt | paste - thisistest.txt`|
| 42 | using stdin for paste throws error of no input stream | `grep test thisistext.txt | paste`|
| 43 | Globbing does not work if a relative file path is specified | `cat folder/*` | |
| 44 | Globing fail when absolute file path is specified | `grep pass /shell-not-pass.txt` | |
| 45 | Globing does not work with grep | `grep p ./*` | | 
| 46 | Cannot handle regex `???`: throws invalid pattern while it should return file with three characters | `ls | grep ???`
| 47 | Grep with missing argument should throw error instead of going infinite loop | `grep omg` | |
| 48 | Grep with empty string argument will go into infinite loop | `grep "" thisistest.txt` | |
| 49 | Cannot grep file contain only spaces | `grep " " spaces.txt` | |
| 50 | Grep invert spaces printed extra empty lines |`grep -v " " empty.txt` | |
| 51 | Grep with quoted '-v' will cause infinite loop | `grep "-v" pom.xml` ||
| 52 | Semicolon after a command does not work | `echo a;`||
| 53 | Command substitution fail when the following command is given. Linux Bash will evaluate as empty echo.  | \`echo\` || 