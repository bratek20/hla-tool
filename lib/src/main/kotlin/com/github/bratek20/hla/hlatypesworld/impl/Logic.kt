package com.github.bratek20.hla.hlatypesworld.impl

import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.*

import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import com.github.bratek20.hla.mvvmtypesmappers.impl.ModelToViewModelTypeMapper
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType

class HlaTypesWorldApiLogic(
    private val populators: Set<HlaTypesWorldPopulator>,
    private val viewModelTypesPopulator: ViewModelTypesPopulator,
    private val viewTypesPopulator: ViewTypesPopulator,
    private val typesWorldApi: TypesWorldApi
): HlaTypesWorldApi {
    lateinit var apiTypeFactory: ApiTypeFactory
    override fun populate(group: ModuleGroup) {
        viewModelTypesPopulator.apiTypeFactory = apiTypeFactory
        viewTypesPopulator.mapper = ModelToViewModelTypeMapper(apiTypeFactory, typesWorldApi)

        val modules = BaseModuleGroupQueries(group).modules
        populators
            .sortedBy { it.getOrder() }
            .forEach { it.populate(modules) }
    }
}

class HlaTypesWorldQueriesLogic(
    private val typesWorldApi: TypesWorldApi
): HlaTypesWorldQueries {
    override fun getAll(module: ModuleName, submodule: SubmoduleName): List<WorldType> {
        return typesWorldApi.getAllTypes().filter {
            val hlaPath = it.getPath().asHla()
            hlaPath.getModuleName() == module && hlaPath.getSubmoduleName() == submodule
        }
    }
}