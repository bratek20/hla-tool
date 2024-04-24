package pl.bratek20.hla.parsing.impl

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.model.*
import pl.bratek20.hla.parsing.api.HlaModulesParser

class HlaModulesParserImpl: HlaModulesParser {
    override fun parse(path: Path): List<HlaModule> {
        val directories = DirectoriesLogic()

        val modulesDir = directories.readDirectory(path)
        return modulesDir.files.map { parseModuleFile(it) }
    }

    private fun parseModuleFile(file: File): HlaModule {
        val moduleName = ModuleName(file.name.split(".module").get(0))
        val elements = parseElements(file.content)
        val valueObjects = parseValueObjects(elements)
        return HlaModule(
            name = moduleName,
            simpleValueObjects = valueObjects.simple,
            complexValueObjects = valueObjects.complex,
            interfaces = emptyList()
        )
    }

    open class ParsedElement(
        val indent: Int
    )

    class Section(
        indent: Int,
        val name: String,
        val elements: MutableList<ParsedElement> = mutableListOf()
    ) : ParsedElement(indent) {
        fun addElement(element: ParsedElement) {
            elements.add(element)
        }
    }

    class Assignment(
        indent: Int,
        val name: String,
        val value: String
    ) : ParsedElement(indent)

    private fun parseElements(content: FileContent): List<ParsedElement> {
        val initialElements = content.lines.map { parseElement(it) }
        val result = mutableListOf<ParsedElement>()
        var currentIndent = 0
        var lastSection: Section? = null

        for (element in initialElements) {
            if(element is Section) {
                if(element.indent > currentIndent) {
                    lastSection?.addElement(element)
                } else {
                    result.add(element)
                }
                lastSection = element
                currentIndent = element.indent
            } else {
                lastSection?.addElement(element)
            }
        }

        return result
    }

    private fun parseElement(line: String): ParsedElement {
        val indent = line.takeWhile { it == ' ' }.length
        val noIndentLine = line.trim()

        if(noIndentLine.contains(":"))  {
            noIndentLine.split(":").let {
                return Assignment(indent, it[0], it[1].trim())
            }
        } else {
            return Section(indent, noIndentLine)
        }
    }

    data class ValueObjects(
        val simple: List<SimpleValueObject>,
        val complex: List<ComplexValueObject>
    )
    private fun parseValueObjects(elements: List<ParsedElement>): ValueObjects {
        val voSection = elements.find { it is Section && it.name == "ValueObjects" } as Section?
        val simple = voSection?.elements?.filterIsInstance<Assignment>()?.map {
            SimpleValueObject(
                name = it.name,
                typeName = it.value
            )
        } ?: emptyList()

        val complex = voSection?.elements?.filterIsInstance<Section>()?.map {
            parseComplexValueObject(it)
        } ?: emptyList()

        return ValueObjects(
            simple = simple,
            complex = complex
        )
    }

    private fun parseComplexValueObject(section: Section): ComplexValueObject {
        return ComplexValueObject(
            name = section.name,
            fields = section.elements.filterIsInstance<Assignment>().map {
                Field(
                    name = it.name,
                    type = Type(
                        name = it.value
                    )
                )
            }
        )
    }
}