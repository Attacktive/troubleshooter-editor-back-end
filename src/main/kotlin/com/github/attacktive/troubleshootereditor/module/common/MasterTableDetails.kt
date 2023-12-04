package com.github.attacktive.troubleshootereditor.module.common

data class MasterTableDetails(val tableName: String, val idColumnName: String, val indexColumnName: String = "masterIndex", val valueColumnName: String = "propValue")
