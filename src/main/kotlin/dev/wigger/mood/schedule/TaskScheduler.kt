package dev.wigger.mood.schedule

import dev.wigger.mood.user.UserService
import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.ScheduledExecution
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class TaskScheduler {
    @Inject
    lateinit var userService: UserService

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    fun deleteUnverifiedAccount(execution: ScheduledExecution) {
        userService.deleteUnverifiedAndOldUsers()
    }
    
    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    fun updateResetTokenToNull(execution: ScheduledExecution) {
        userService.updateResetTokenToNull()
    }
}
