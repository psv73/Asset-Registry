package net.psv73.assetregistry.repository;

import jakarta.persistence.EntityManager;
import net.psv73.assetregistry.PostgresIntegrationTest;
import net.psv73.assetregistry.entity.Asset;
import net.psv73.assetregistry.entity.Client;
import net.psv73.assetregistry.entity.DeviceType;
import net.psv73.assetregistry.entity.Manufacturer;
import net.psv73.assetregistry.entity.Model;
import net.psv73.assetregistry.entity.Os;
import net.psv73.assetregistry.entity.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
class AssetRepositoryIT extends PostgresIntegrationTest {

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    EntityManager em;

    private Client client;
    private Model model;
    private Status status;
    private Os os;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        // build one TRUNCATE statement for all public tables except flyway history
        String truncateSql = jdbc.queryForList("""
            SELECT 'TRUNCATE TABLE ' ||
                   string_agg(quote_ident(tablename), ', ') ||
                   ' RESTART IDENTITY CASCADE'
            FROM pg_tables
            WHERE schemaname = 'public'
              AND tablename <> 'flyway_schema_history'
            """, String.class).get(0);

        jdbc.execute(truncateSql);

        client = em.merge(Client.builder().name("ACME").email("acme@example.com").build());

        Manufacturer manufacturer = em.merge(
                Manufacturer.builder().code("LENOVO").name("Lenovo").build()
        );

        DeviceType deviceType = em.merge(
                DeviceType.builder().code("LAPTOP").name("Laptop").build()
        );

        model = em.merge(
                Model.builder().manufacturer(manufacturer).deviceType(deviceType).name("ThinkPad T16").build()
        );

        status = em.merge(
                Status.builder().code("IN_USE").label("In use").build()
        );

        os = em.merge(
                Os.builder().code("WIN").name("Windows").version("11").build()
        );

        em.flush();
    }

    @Test
    void findAll_shouldReturnPagedResult() {
        // given
        for (int i = 1; i <= 25; i++) {
            assetRepository.save(Asset.builder()
                    .client(client)
                    .model(model)
                    .status(status)
                    .os(os)
                    .inventoryCode(String.format("INV-%03d", i))
                    .serialNumber(String.format("SN-%03d", i))
                    .hostname("host-" + i)
                    .build());
        }

        // when
        Page<Asset> page0 = assetRepository.findAll(PageRequest.of(0, 10));
        Page<Asset> page2 = assetRepository.findAll(PageRequest.of(2, 10));

        // then
        assertThat(page0.getTotalElements()).isEqualTo(25);
        assertThat(page0.getContent()).hasSize(10);

        assertThat(page2.getTotalElements()).isEqualTo(25);
        assertThat(page2.getContent()).hasSize(5);
    }

    @Test
    void findAllByInventoryCode_shouldReturnPageWithSingleMatch() {
        // given
        assetRepository.save(Asset.builder()
                .client(client)
                .model(model)
                .status(status)
                .os(os)
                .inventoryCode("INV-777")
                .serialNumber("SN-777")
                .hostname("host-777")
                .build());

        // when
        Page<Asset> page = assetRepository.findAllByInventoryCode("INV-777", PageRequest.of(0, 20));

        // then
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getInventoryCode()).isEqualTo("INV-777");
    }
}