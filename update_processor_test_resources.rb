# A script to update processor test files in the "src/test/resources" package with the latest test output.
# This is useful when changes are made to the annotation processor and all the tests need to have their expected values updated.
#
# You may have to run this multiple times for the tests that output several generated source files.
# In those cases the test report only details the first failure, so you will have to run `./gradlew test`
# again to generate a new test report before running this script again.

require 'rubygems'
require 'nokogiri'

def updateTestClass(test_class_result)
  page = Nokogiri::HTML(open(test_class_result))

  # Failing processor tests have their output in a <pre></pre> block
  page.css('pre').each do |preBlock|
    # Just a sanity check to make sure the pre block we're looking at is a processor source output
    if preBlock.include? "Source declared the same top-level types of an expected source"
      puts "Pre block did not contain source. (#{test_class_result}"
      next
    end

    # We expect to see a line like:
    # Expected file: </Users/eli_hart/repos/epoxy/epoxy-processortest/build/intermediates/classes/test/debug/ModelWithViewClickListener_.java>;
    # Which tells us where the original processor test file lives
    expected_file_match = /Expected file: <([^>]*)>/m.match(preBlock)
    if expected_file_match.nil? || expected_file_match.captures.empty?
      puts "Could not find expected file name in pre block (#{test_class_result})"
      next
    end

    # The test copies the source file to the build folder. We need to modify the original file to update its expected source
    expected_source_file_path = expected_file_match.captures[0].sub "build/intermediates/classes/test/debug", "src/test/resources"

    # The error message includes the source code that was generated. We use a regex to extract the source from the following expected pattern
    #
    # Actual Source:
    # =================
    # ... Source code here
    #     at com.google.testing.compile.JavaSourcesSubject$CompilationClause.failWithCandidate(JavaSourcesSubject.java:224)
    # at com.google.testing.compile.JavaSourcesSubject$CompilationClause.parsesAs(JavaSourcesSubject.java:186)
    # at com.google.testing.compile.JavaSourcesSubject.parsesAs(JavaSourcesSubject.java:95)
    actual_source_match = /Actual Source:[\s]*=*[\s]*(package.*?})[\s]*at com\.google/m.match(preBlock)
    if actual_source_match.nil? || actual_source_match.captures.empty?
      puts "Could not find actual source in pre block (#{test_class_result})"
      next
    end

    puts "Updating class: #{expected_source_file_path.split('/')[-1]}"

    # Finally we simply overwrite the original expected test source with the actual test output in order to update it
    actual_source = actual_source_match.captures[0]
    File.open(expected_source_file_path, "w") do |f|
      f.write actual_source
    end
  end
end

# Looks through each module's build folder for debug test results
Dir.glob("*/build/reports/tests/testDebugUnitTest/classes/*.html") do |test_class_result|
  updateTestClass(test_class_result)
end