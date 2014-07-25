package info.archinnov.achilles.demo.service;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import org.apache.cassandra.utils.UUIDGen;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import info.archinnov.achilles.demo.entity.Message;
import info.archinnov.achilles.demo.entity.User;
import info.archinnov.achilles.junit.AchillesResource;
import info.archinnov.achilles.junit.AchillesResourceBuilder;
import info.archinnov.achilles.persistence.PersistenceManager;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {

    @Rule
    public AchillesResource resource = AchillesResourceBuilder
            .withEntityPackages("info.archinnov.achilles.demo.entity")
            .tablesToTruncate(Message.TABLE_NAME)
            .truncateBeforeAndAfterTest().build();

    private PersistenceManager manager = resource.getPersistenceManager();

    private Session session = resource.getNativeSession();

    private MessageService service = new MessageService();

    @Before
    public void setUp() {
        service.manager = this.manager;
    }

    @Test
    public void should_create_message() throws Exception {
        //Given
        UUID date = UUIDGen.getTimeUUID();

        //When
        service.createMessage("emc2",date,"max_plank","Hey buddy, I've just discover the secret of the universe: E = mc2");

        //Then
        final Select.Where select = select().from(Message.TABLE_NAME).where(eq("login", "emc2"));

        final Row row = session.execute(select).one();

        assertThat(row).isNotNull();
        assertThat(row.getUUID("date")).isEqualTo(date);
        assertThat(row.getString("interlocutor")).isEqualTo("max_plank");
        assertThat(row.getString("content")).isEqualTo("Hey buddy, I've just discover the secret of the universe: E = mc2");
    }

    @Test
    public void should_fetch_some_messages() throws Exception {
        //Given
        UUID date1 = UUIDGen.getTimeUUID(1000);
        UUID date2 = UUIDGen.getTimeUUID(2000);
        UUID date3 = UUIDGen.getTimeUUID(3000);
        UUID date4 = UUIDGen.getTimeUUID(4000);
        UUID date5 = UUIDGen.getTimeUUID(5000);

        final Insert insert = insertInto(Message.TABLE_NAME)
                .value("login", bindMarker())
                .value("date", bindMarker())
                .value("interlocutor", bindMarker())
                .value("content", bindMarker());

        session.execute(insert.getQueryString(),"emc2",date1,"max_plank","Message 1");
        session.execute(insert.getQueryString(),"emc2",date2,"max_plank","Message 2");
        session.execute(insert.getQueryString(),"emc2",date3,"max_plank","Message 3");
        session.execute(insert.getQueryString(),"emc2",date4,"max_plank","Message 4");
        session.execute(insert.getQueryString(),"emc2",date5,"max_plank","Message 5");

        //When
        final List<Message> messages = service.fetchMessages("emc2", date5, 3);

        //Then
        assertThat(messages.get(0).getContent()).isEqualTo("Message 4");
        assertThat(messages.get(1).getContent()).isEqualTo("Message 3");
        assertThat(messages.get(2).getContent()).isEqualTo("Message 2");
    }


}
