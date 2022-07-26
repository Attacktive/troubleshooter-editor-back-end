package com.github.attacktive.troubleshootereditor.sqlite

import org.junit.jupiter.api.Test

class SqliteServiceTest(private val sqliteService: SqliteService) {
	@Test
	fun testBasic() {
		/*
		 * Before running it, put 'game.save' file to 'app.file.path-to-upload'
		 */
		sqliteService.run("game.save")
	}
}
