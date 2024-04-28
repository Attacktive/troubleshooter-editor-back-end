package com.github.attacktive.troubleshootereditor.domain.common

import com.github.attacktive.troubleshootereditor.domain.company.InboundCompany
import com.github.attacktive.troubleshootereditor.domain.item.InboundItem
import com.github.attacktive.troubleshootereditor.domain.roster.Roster

data class InboundSaveData(val company: InboundCompany, val rosters: List<Roster>, val items: List<InboundItem>)
