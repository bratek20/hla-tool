package com.github.bratek20.utils.directory

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.github.bratek20.utils.directory.api.FileName
import com.github.bratek20.utils.directory.api.Path
import io.kotest.core.spec.style.StringSpec

class PathTest : StringSpec({

    "normalized should replace backslashes with forward slashes" {
        val path = Path("C:\\Users\\User")
        path.value shouldBe "C:/Users/User"
    }

    "add(path) should concatenate paths correctly" {
        val path1 = Path("C:/Users")
        val path2 = Path("Documents")
        val result = path1.add(path2)
        result.value shouldBe "C:/Users/Documents"
    }

    "add(path) with empty path should return original path" {
        val path1 = Path("C:/Users")
        val path2 = Path("")
        val result = path1.add(path2)
        result.value shouldBe "C:/Users"
    }

    "add(fileName) should concatenate path and filename correctly" {
        val path = Path("C:/Users")
        val fileName = FileName("file.txt")
        val result = path.add(fileName)
        result.value shouldBe "C:/Users/file.txt"
    }

    "equals should return true for identical paths" {
        val path1 = Path("C:/Users")
        val path2 = Path("C:/Users")
        path1 shouldBe path2
    }

    "equals should return false for different paths" {
        val path1 = Path("C:/Users")
        val path2 = Path("C:/Documents")
        path1 shouldNotBe path2
    }

    "hashCode should return same value for identical paths" {
        val path1 = Path("C:/Users")
        val path2 = Path("C:/Users")
        path1.hashCode() shouldBe path2.hashCode()
    }

    "toString should return normalized path" {
        val path = Path("C:\\Users\\User")
        path.toString() shouldBe "C:/Users/User"
    }

    "should subtract path" {
        val path1 = Path("C:/Users")
        val path2 = Path("C:/Users/Documents")
        val result = path2.subtract(path1)
        result.value shouldBe "Documents"
    }

    "should throw exception when path not subtractable" {
        val path1 = Path("C:/Users")
        val path2 = Path("C:/Documents")
        val exception = shouldThrow<IllegalArgumentException> {
            path2.subtract(path1)
        }
        exception.message shouldBe "Path $path2 is not subtractable from $path1"
    }
})