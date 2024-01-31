package com.arsahub.backend.repositories

import com.arsahub.backend.models.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, Long>
