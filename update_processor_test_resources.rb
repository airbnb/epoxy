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

    if preBlock.to_s.include? "[Robolectric]"
      # This block is a robolectric info output, and is not a test failure
      next
    end

    # Just a sanity check to make sure the pre block we're looking at is a processor source output
    if preBlock.to_s.include? "Source declared the same top-level types of an expected source"
      puts "Pre block did not contain source. (#{test_class_result}"
      next
    end

    # We expect to see a line like:
    # Expected file: </ModelWithViewClickListener_.java>;
    # Which tells us where the original processor test file lives
    expected_file_match = /Expected file: <([^>]*)>/m.match(preBlock)
    if expected_file_match.nil? || expected_file_match.captures.empty?
      puts "Could not find expected file name in pre block (#{test_class_result})"
      puts preBlock.class
      puts preBlock
      next
    end

    # The test copies the source file to the build folder. We need to modify the original file to update its expected source
    expected_source_file_name = expected_file_match.captures[0]
    module_path = test_class_result.split("build/reports/").first
    expected_source_file_path = module_path + "src/test/resources" + expected_source_file_name

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

    puts "Full path #{expected_source_file_path}"
    puts "Updating class: #{expected_source_file_path.split('/')[-1]}"

    # Finally we simply overwrite the original expected test source with the actual test output in order to update it
    actual_source = actual_source_match.captures[0]
    File.write(expected_source_file_path, actual_source)
  end
end

# Looks through each module's build folder for debug test results
Dir.glob("*/build/reports/tests/testDebugUnitTest/classes/*.html") do |test_class_result|
  updateTestClass(test_class_result)
end
