package components.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LongMapper {

  /**
   * Return the Long value from the column of the result set.
   * This is necessary since the standard ResultSet.getLong method returns 0 if the actual database value is null.
   * See https://stackoverflow.com/a/38241632
   *
   * @param resultSet
   * @param column
   * @return
   * @throws SQLException
   */
  public static Long getLong(ResultSet resultSet, String column) throws SQLException {
    return Optional.ofNullable(resultSet.getBigDecimal(column))
        .map(BigDecimal::longValue).orElse(null);
  }

}
