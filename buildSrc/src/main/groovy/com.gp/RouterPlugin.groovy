import org.gradle.api.Plugin
import org.gradle.api.Project

public class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def log = project.logger
        log.error "========================"
        log.error "完整的MyPlugin，开始修改Class!"
        log.error "========================"
    }
}