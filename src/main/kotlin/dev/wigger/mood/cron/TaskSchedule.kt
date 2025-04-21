package dev.wigger.mood.cron

import dev.wigger.mood.user.UserRepository
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class TaskSchedule {
    @Inject
    private lateinit var userRepository: UserRepository

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    fun deleteUnverifiedUsers() {
        userRepository.deleteUnverifiedUsers()
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    fun resetToken() {
        userRepository.updateResetTokenToNull()
        userRepository.updateSharingTokenToNull()
    }
}
