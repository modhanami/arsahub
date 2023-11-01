package com.arsahub.backend.controllers

import com.arsahub.backend.repositories.ActionRepository
import com.arsahub.backend.repositories.RuleRepository
import com.arsahub.backend.repositories.TriggerRepository
import com.arsahub.backend.repositories.TriggerTypeRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/rules")
class RuleController(
    private val ruleRepository: RuleRepository,
    private val triggerRepository: TriggerRepository,
    private val triggerTypeRepository: TriggerTypeRepository,
    private val actionRepository: ActionRepository
) {
    //    {
//    "title": "Fake Event 1",
//    "description": "This is a test event.",
//    "trigger": {
//        "id": 1,
//        "type": "times",
//        "params": {
//            "times": 3
//        }
//    },
//    "effect": {
//        "id": 1,
//        "params": {
//            "amount": 50
//        }
//    }
//}

}