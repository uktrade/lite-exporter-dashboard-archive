package components.service;

import com.google.inject.Inject;
import components.dao.DraftRfiResponseDao;
import components.dao.RfiResponseDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import models.DraftRfiResponse;
import models.File;
import models.RfiResponse;
import models.enums.RoutingKey;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RfiResponseServiceImpl implements RfiResponseService {

  private final UserService userService;
  private final RfiResponseDao rfiResponseDao;
  private final DraftRfiResponseDao draftRfiResponseDao;
  private final MessagePublisher messagePublisher;

  @Inject
  public RfiResponseServiceImpl(UserService userService, RfiResponseDao rfiResponseDao, DraftRfiResponseDao draftRfiResponseDao, MessagePublisher messagePublisher) {
    this.userService = userService;
    this.rfiResponseDao = rfiResponseDao;
    this.draftRfiResponseDao = draftRfiResponseDao;
    this.messagePublisher = messagePublisher;
  }

  @Override
  public void insertRfiResponse(String rfiId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(rfiId, files);
    RfiResponse rfiResponse = new RfiResponse(rfiId, userService.getCurrentUser().getId(), Instant.now().toEpochMilli(), message, attachments);
    rfiResponseDao.insertRfiResponse(rfiResponse);
    draftRfiResponseDao.deleteDraftRfiResponse(rfiId);
    messagePublisher.sendMessage(RoutingKey.RFI_REPLY, rfiResponse);
  }

  private List<File> getAttachments(String rfiId, List<UploadFile> uploadFiles) {
    List<File> files;
    if (!CollectionUtils.isEmpty(uploadFiles)) {
      files = uploadFiles.stream()
          .map(uploadFile -> new File(UUID.randomUUID().toString(), uploadFile.getOriginalFilename(), uploadFile.getDestinationPath(), System.currentTimeMillis()))
          .collect(Collectors.toList());
    } else {
      files = new ArrayList<>();
    }
    DraftRfiResponse draftRfiResponse = draftRfiResponseDao.getDraftRfiResponse(rfiId);
    if (draftRfiResponse != null) {
      files.addAll(draftRfiResponse.getAttachments());
    }
    return files;
  }

}
