package dev.wigger.mood.schedule

import dev.wigger.mood.user.UserRepository
import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.ScheduledExecution
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class TaskSchedule {
    @Inject
    private lateinit var userRepository: UserRepository

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    fun midnight(execution: ScheduledExecution) {
        userRepository.deleteUnverifiedAndOldUsers()
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    fun midnightToken(execution: ScheduledExecution) {
        userRepository.updateResetTokenToNull()
        userRepository.updateSharingTokenToNull()
    }
}
