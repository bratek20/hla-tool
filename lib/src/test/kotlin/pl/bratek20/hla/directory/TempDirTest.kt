package pl.bratek20.hla.directory

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import pl.bratek20.hla.directory.api.Path

open class TempDirTest {
    @TempDir
    private lateinit var tempDirNio: java.nio.file.Path
    protected lateinit var tempDir: Path

    @BeforeEach
    fun setup() {
        tempDir = Path(tempDirNio.toAbsolutePath().toString())
    }
}