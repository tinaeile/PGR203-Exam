package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MemberDao extends AbstractDao<Member> {

    public MemberDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Member member) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO members (name, email) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, member.getName());
                statement.setString(2, member.getEmail());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    member.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public Member retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM members WHERE id = ?");
    }

    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        return member;
    }

    public List<Member> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<Member> members = new ArrayList<>();
                    while (rs.next()) {
                        members.add(mapRow(rs));
                    }
                    return members;
                }
            }
        }
    }
}
