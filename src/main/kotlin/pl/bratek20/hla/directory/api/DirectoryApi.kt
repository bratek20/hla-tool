package pl.bratek20.hla.directory.api

data class CompareResult(
    val same: Boolean,
    val differences: List<String>
)

interface DirectoryApi {
    fun readDirectory(path: Path): Directory

    //TODO test for that
    fun writeDirectory(path: Path, directory: Directory)

    fun compare(dir1: Directory, dir2: Directory): CompareResult
}