package components.dao.jdbi;

import components.dao.mapper.RfiWithdrawalRSMapper;
import models.RfiWithdrawal;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface RfiWithdrawalJDBIDao {

  @Mapper(RfiWithdrawalRSMapper.class)
  @SqlQuery("SELECT * FROM RFI_WITHDRAWAL WHERE RFI_ID in (<rfiIds>)")
  List<RfiWithdrawal> getRfiWithdrawals(@BindIn("rfiIds") List<String> rfiIds);

  @SqlUpdate("INSERT INTO RFI_WITHDRAWAL (ID,  RFI_ID, CREATED_BY_USER_ID, CREATED_TIMESTAMP, RECIPIENT_USER_IDS, MESSAGE) VALUES " +
      "                                 (:id, :rfiId, :createdByUserId,   :createdTimestamp, :recipientUserIds,  :message)")
  void insertRfiWithdrawal(@Bind("id") String id,
                           @Bind("rfiId") String rfiId,
                           @Bind("createdByUserId") String createdByUserId,
                           @Bind("createdTimestamp") Long createdTimestamp,
                           @Bind("recipientUserIds") String recipientUserIds,
                           @Bind("message") String message);

  @SqlUpdate("DELETE FROM RFI_WITHDRAWAL")
  void truncateTable();

  @SqlUpdate("DELETE FROM RFI_WITHDRAWAL WHERE RFI_ID = :rfiId")
  void deleteRfiWithdrawalByRfiId(@Bind("rfiId") String rfiId);

}
