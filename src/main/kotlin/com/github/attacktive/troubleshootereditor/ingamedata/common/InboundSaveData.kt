package com.github.attacktive.troubleshootereditor.ingamedata.common

import com.github.attacktive.troubleshootereditor.ingamedata.company.domain.InboundCompany
import com.github.attacktive.troubleshootereditor.ingamedata.item.domain.InboundItem
import com.github.attacktive.troubleshootereditor.ingamedata.roster.domain.InboundRoster

data class InboundSaveData(val company: InboundCompany, val rosters: List<InboundRoster>, val items: List<InboundItem>)
