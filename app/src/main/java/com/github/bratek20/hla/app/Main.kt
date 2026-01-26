package com.github.bratek20.hla.app

import com.github.bratek20.architecture.context.spring.SpringContextBuilder
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.facade.api.AllModulesOperationArgs.Companion.create
import com.github.bratek20.hla.facade.api.ModuleOperationArgs.Companion.create
import com.github.bratek20.hla.facade.context.FacadeImpl
import com.github.bratek20.logs.context.Slf4jLogsImpl
import com.github.bratek20.utils.directory.api.Path
import com.github.bratek20.utils.directory.context.DirectoryImpl

fun main(args: Array<String>) {
    val context = SpringContextBuilder()
        .withModules(
            Slf4jLogsImpl(),

            DirectoryImpl(),
            FacadeImpl()
        )
        .build()

    val facade = context.get(HlaFacade::class.java)

    when (val operationName = args[0]) {
        "start" -> facade.startModule(parseModuleOperationArgs(args))
        "update" -> facade.updateModule(parseModuleOperationArgs(args))
        "updateAll" -> facade.updateAllModules(parseAllModulesOperationArgs(args))
        "startAll" -> facade.startAllModules(parseAllModulesOperationArgs(args))
        else -> throw IllegalArgumentException("Unknown operation: $operationName")
    }
}

private fun parseModuleOperationArgs(args: Array<String>): ModuleOperationArgs {
    return create(
        Path(args[1]),
        ProfileName(args[2]),
        ModuleName(args[3])
    )
}

private fun parseAllModulesOperationArgs(args: Array<String>): AllModulesOperationArgs {
    return create(
        Path(args[1]),
        ProfileName(args[2])
    )
}
