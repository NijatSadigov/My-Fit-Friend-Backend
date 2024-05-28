package com.MyFitFriend.db

import com.MyFitFriend.data.model.*
import com.MyFitFriend.di.appModule
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DietGroupServiceTest : KoinTest {

    private val dietGroupService: DietGroupService by inject()

    @BeforeAll
    fun beforeAll() {
        startKoin {
            modules(appModule)
        }
    }

    @BeforeEach
    fun setup() {
        // Initialize in-memory database
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            // Create tables
            SchemaUtils.create(Users, DietGroups, DietGroupMembers, DietGroupRequests)

            // Insert a test user
            Users.insert {
                it[username] = "Test User"
                it[email] = "test@example.com"
                it[passwordHash] = "hashedPassword"
                it[weight] = 70.0
                it[height] = 175.0
                it[activityLevel] = 1
                it[age] = 30
                it[sex] = true
                it[lastEditDate] = System.currentTimeMillis()
            }
        }
    }

    @AfterEach
    fun teardown() {
        // Clear the database after each test
        transaction {
            SchemaUtils.drop(DietGroupRequests, DietGroupMembers, DietGroups, Users)
        }
    }

    @AfterAll
    fun afterAll() {
        stopKoin()
    }

    @Test
    fun `test createDietGroup`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        assertTrue(groupId > 0)
    }

    @Test
    fun `test getDietGroupById`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)

        val result = dietGroupService.getDietGroupById(groupId)
        assertNotNull(result)
        assertEquals("Healthy Diet", result?.groupName)
    }

    @Test
    fun `test updateDietGroupName`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)

        val updatedGroup = dietGroupService.updateDietGroupName(groupId, "New Healthy Diet")
        assertNotNull(updatedGroup)
        assertEquals("New Healthy Diet", updatedGroup?.groupName)
    }

    @Test
    fun `test deleteDietGroup`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)

        val result = dietGroupService.deleteDietGroup(groupId)
        assertTrue(result)
    }

    @Test
    fun `test addUserToDietGroup`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)

        val result = dietGroupService.addUserToDietGroup(1, groupId)
        assertTrue(result)
    }

    @Test
    fun `test removeUserFromDietGroup`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        dietGroupService.addUserToDietGroup(1, groupId)

        val result = dietGroupService.removeUserFromDietGroup(1, groupId)
        assertTrue(result)
    }

    @Test
    fun `test getUserDietGroups`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        dietGroupService.addUserToDietGroup(1, groupId)

        val groups = dietGroupService.getUserDietGroups(1)
        assertNotNull(groups)
        assertTrue(groups.isNotEmpty())
    }

    @Test
    fun `test getUsersOfGroupByGroupId`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        dietGroupService.addUserToDietGroup(1, groupId)

        val users = dietGroupService.getUsersOfGroupByGroupId(groupId)
        assertNotNull(users)
        assertTrue(users.contains(1))
    }

    @Test
    fun `test isUserOwner`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)

        val result = dietGroupService.isUserOwner(1, groupId)
        assertTrue(result)
    }

    @Test
    fun `test doMemberExistAlready`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        dietGroupService.addUserToDietGroup(1, groupId)

        val result = dietGroupService.doMemberExistAlready(1, groupId)
        assertTrue(result)
    }

    @Test
    fun `test createRequest`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)

        val result = dietGroupService.createRequest(2, groupId)
        assertTrue(result)
    }

    @Test
    fun `test answerDeleteRequest`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        dietGroupService.createRequest(2, groupId)

        val requests = dietGroupService.getRequestsByUserId(2)
        val requestId = requests.first().requestId

        val result = dietGroupService.answerDeleteRequest(true, requestId, 2)
        assertTrue(result)
    }

    @Test
    fun `test removeAllRequestForGroup`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        dietGroupService.createRequest(2, groupId)

        val result = dietGroupService.removeAllRequestForGroup(groupId)
        assertTrue(result)
    }

    @Test
    fun `test getRequestsByUserId`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        dietGroupService.createRequest(2, groupId)

        val requests = dietGroupService.getRequestsByUserId(2)
        assertNotNull(requests)
        assertTrue(requests.isNotEmpty())
    }

    @Test
    fun `test hasAlreadyInvited`() = runBlocking {
        val dietGroup = DietGroup(
            groupName = "Healthy Diet",
            groupOwnerId = 1,
            groupId = null
        )
        val groupId = dietGroupService.createDietGroup(dietGroup)
        dietGroupService.createRequest(2, groupId)

        val result = dietGroupService.hasAlreadyInvited(2, groupId)
        assertTrue(result)
    }
}
