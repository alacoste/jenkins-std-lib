import org.junit.*
import static org.junit.Assert.*
import com.lesfurets.jenkins.unit.*
 
class bashTest extends BasePipelineTest {
    def bash
 
    @Before
    void setUp() {
        super.setUp()
        binding.setVariable('log', loadScript('vars/log.groovy'))
        helper.registerAllowedMethod("ansiColor", [String.class, Closure.class], null)
        helper.registerAllowedMethod("println", [String.class], null)
        bash = loadScript("vars/bash.groovy")
    }

    @Test
    void 'Should format scripts correctly.'() {

        String userScript = 'echo "hey"'
        String script = bash.formatScript(userScript)

        assertTrue('Should have set bash shabang.', script.contains('#!/bin/bash'))
        assertTrue('Should have sourced bashrc.', script.contains('source $HOME/.bashrc > /dev/null 2>&1 || true'))
        assertTrue('Should have set exit on first error.', script.contains('{ set -e; } > /dev/null 2>&1'))
        assertTrue('Should have set output to console.', script.contains('exec 2> >(tee -a stderr stdall) 1> >(tee -a stdout stdall)'))
        assertTrue('Should have not output the bash commands.', script.contains('{ set +x; } > /dev/null 2>&1'))
        assertTrue('Should have included our script.', script.contains(userScript))
        assertEquals('Should indent returned script.', script, script.stripIndent())

        binding.setVariable('env', [PIPELINE_LOG_LEVEL:"DEBUG"])
        script = bash.formatScript(userScript, false, false)
        assertTrue('Should not exit on first error.', script.contains('{ set +e; } > /dev/null 2>&1'))
        assertTrue('Should not output to console.', script.contains('exec 3>/dev/null 2> >(tee -a stderr stdall >&3) 1> >(tee -a stdout stdall >&3)'))
        assertTrue('Should output the bash commands.', script.contains('{ set -x; } > /dev/null 2>&1'))
        assertEquals('Should have debug logging.', 2, helper.methodCallCount('debug'))
    }

    @Ignore
    @Test
    void 'Should ignore errors.'(){
      helper.registerAllowedMethod("sh", [Map.class], {c -> 4})
      helper.registerAllowedMethod("readFile", [String.class], {'test'})
      String userScript = 'echo "hey"'
      def result = bash.ignoreErrors(userScript)
      print('Results: ')
      print(result)

      printCallStack()
    }

    @Test
    void 'Should get script output.'(){
      
      helper.registerAllowedMethod("readFile", [String.class], { file ->
        if(file == 'stdout'){
          return 'stdOut'
        }else if(file == 'stderr'){
          return 'stdErr'
        }else{
          return 'output'
        }
      })

      def(String stdOut, String stdErr, String output) = bash.getOutputs()

      assertTrue('Should read the stdout file.', helper.getCallStack()[1].args[0].toString().contains('stdout'))
      assertTrue('Should read the stderr file.', helper.getCallStack()[2].args[0].toString().contains('stderr'))
      assertTrue('Should read the stdall file.', helper.getCallStack()[3].args[0].toString().contains('stdall'))

      assertEquals('Should return stdOut', stdOut, 'stdOut')
      assertEquals('Should return stdErr', stdErr, 'stdErr')
      assertEquals('Should return output', output, 'output')
      assertTrue('Should cleanup log files.', helper.getCallStack()[4].args[0].toString().contains('rm stdout stderr stdall'))
    }
}