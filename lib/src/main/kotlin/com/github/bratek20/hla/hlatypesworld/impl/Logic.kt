package com.github.bratek20.hla.hlatypesworld.impl

import com.github.bratek20.hla.hlatypesworld.api.*

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import com.github.bratek20.hla.generation.impl.core.viewmodel.ModelToViewModelTypeMapper
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.typesworld.api.TypesWorldApi

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