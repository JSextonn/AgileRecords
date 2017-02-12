package test.groovy

import junit.framework.Test

/**
 * Will act as an intersection point for all test classes.
 */
class AllTestCases {
    static Test suite(){
        def allTests = new GroovyTestSuite()
        allTests.addTestSuite(AdminTests.class)
        allTests.addTestSuite(StudentTests.class)
        return allTests
    }
}
