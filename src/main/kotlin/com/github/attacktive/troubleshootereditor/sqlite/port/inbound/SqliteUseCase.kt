package com.github.attacktive.troubleshootereditor.sqlite.port.inbound

import java.io.File
import com.github.attacktive.troubleshootereditor.ingamedata.common.InboundSaveData
import com.github.attacktive.troubleshootereditor.ingamedata.common.SaveData

interface SqliteUseCase {
	fun readFileByName(fileName: String): SaveData
	fun readFile(file: File): SaveData

	fun save(fileName: String, inboundSaveData: InboundSaveData): String
	fun applyQuickCheats(fileName: String): String
}
