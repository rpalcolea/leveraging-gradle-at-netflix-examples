package netflix

import com.netflix.nebula.lint.rule.GradleLintRule
import com.netflix.nebula.lint.rule.GradleModelAware
import org.codehaus.groovy.ast.expr.MethodCallExpression

class JcenterCleanupRule extends GradleLintRule implements GradleModelAware {
    String description = "jcenter is going away, let's use maven central"

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        /**
         * repositories {
         *     jcenter()
         * }
         */
        if(call.methodAsString == 'jcenter') {
            addBuildLintViolation("jcenter is going away, let's use maven central!!!", call)
                    .replaceWith(call, 'mavenCentral()')
        }
    }
}
