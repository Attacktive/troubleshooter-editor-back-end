package com.github.attacktive.troubleshootereditor.common.diff

data class PropertiesDiffResult(val inserts: List<Pair<String, String>>, val updates: List<Pair<String, String>>, val deletes: List<String>)
