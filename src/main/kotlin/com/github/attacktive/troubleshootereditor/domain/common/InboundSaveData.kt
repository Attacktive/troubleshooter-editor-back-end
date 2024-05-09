package com.github.attacktive.troubleshootereditor.domain.common

import com.github.attacktive.troubleshootereditor.domain.company.InboundCompany
import com.github.attacktive.troubleshootereditor.domain.item.InboundItem
import com.github.attacktive.troubleshootereditor.domain.roster.InboundRoster

data class InboundSaveData(val company: InboundCompany, val rosters: List<InboundRoster>, val items: List<InboundItem>)
