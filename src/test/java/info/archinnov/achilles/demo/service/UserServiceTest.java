package info.archinnov.achilles.demo.service;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.assertj.core.api.Assertions.assertThat;

import info.archinnov.achilles.exception.AchillesLightWeightTransactionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import info.archinnov.achilles.demo.entity.User;
import info.archinnov.achilles.junit.AchillesResource;
import info.archinnov.achilles.junit.AchillesResourceBuilder;
import info.archinnov.achilles.persistence.PersistenceManager;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Rule
    public AchillesResource resource = AchillesResourceBuilder
            .withEntityPackages("info.archinnov.achilles.demo.entity")
            .tablesToTruncate(User.TABLE_NAME)
            .truncateBeforeAndAfterTest().build();

    private PersistenceManager manager = resource.getPersistenceManager();

    private Session session = resource.getNativeSession();

    private UserService service = new UserService();

    @Before
    public void setUp() {
        service.manager = this.manager;
    }

    @Test
    public void should_create_user() throws Exception {
        //Given

        //When
        service.createUser("emc²", "Albert", "EINSTEIN");

        //Then
        final Row row = session.execute(select().from(User.TABLE_NAME).where(eq("login", "emc²"))).one();

        assertThat(row).isNotNull();
        assertThat(row.getString("login")).isEqualTo("emc²");
        assertThat(row.getString("firstname")).isEqualTo("Albert");
        assertThat(row.getString("lastname")).isEqualTo("EINSTEIN");
    }

    @Test
    public void should_find_user_by_id() throws Exception {
        //Given
        final Insert insert = insertInto(User.TABLE_NAME).value("login", "emc²").value("firstname", "Albert").value("lastname", "EINSTEIN");
        session.execute(insert);

        //When
        final User foundUser = service.findByLogin("emc²");

        //Then
        assertThat(foundUser.getFirstname()).isEqualTo("Albert");
        assertThat(foundUser.getLastname()).isEqualTo("EINSTEIN");
    }


    @Test(expected = AchillesLightWeightTransactionException.class)
    public void should_fail_creating_user_if_already_exist() throws Exception {
        //Given
        service.createUser("emc²", "Albert", "EINSTEIN");

        //When
        service.createUser("emc²", "Albert", "EINSTEIN");
    }
}
