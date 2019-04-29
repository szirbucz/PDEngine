package org.rulez.demokracia.pdengine.choice;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.rulez.demokracia.pdengine.annotations.TestedBehaviour;
import org.rulez.demokracia.pdengine.annotations.TestedFeature;
import org.rulez.demokracia.pdengine.annotations.TestedOperation;
import org.rulez.demokracia.pdengine.dataobjects.VoteAdminInfo;
import org.rulez.demokracia.pdengine.exception.ReportedException;
import org.rulez.demokracia.pdengine.testhelpers.ThrowableTester;

@TestedFeature("Manage votes")
@TestedOperation("modify vote")
@RunWith(MockitoJUnitRunner.class)
public class ChoiceModifyValidationTest extends ChoiceTestBase {

  private static final String VALIDATES_INPUTS = "validates inputs";
  private static final String NEW_CHOICE_NAME = "NewChoiceName";
  private static final String USER = "user";
  private static final String ADMIN = "admin";

  private final Choice choice = new Choice("choiceName", USER);

  @Override
  @Before
  public void setUp() {
    super.setUp();
    vote.addChoice(choice);
  }

  @TestedBehaviour(VALIDATES_INPUTS)
  @Test
  public void invalid_voteId_is_rejected() {
    doThrow(
        new ReportedException(
            "illegal voteId",
            invalidvoteId
        )
    ).when(voteService).getVote(invalidvoteId);

    assertModifyChoice(
        invalidvoteId, vote.getAdminKey(), choice.getId(), NEW_CHOICE_NAME
    ).assertMessageIs("illegal voteId");
  }

  @TestedBehaviour(VALIDATES_INPUTS)
  @Test
  public void invalid_choiceId_is_rejected() {
    final String invalidChoiceId = "invalidChoiceId";

    assertModifyChoice(
        vote.getId(), vote.getAdminKey(), invalidChoiceId, NEW_CHOICE_NAME
    ).assertMessageIs("Illegal choiceId");
  }

  @TestedBehaviour(VALIDATES_INPUTS)
  @Test
  public void invalid_adminKey_is_rejected() {
    final String invalidAdminKey = "invalidAdminKey";

    assertModifyChoice(
        vote.getId(), invalidAdminKey, choice.getId(), NEW_CHOICE_NAME
    ).assertMessageIs("Illegal adminKey");
  }

  @TestedBehaviour("modifies the string of the choice")
  @Test
  public void proper_voteId_choiceId_and_adminKey_does_modify_choice() {
    choiceService.modifyChoice(
        new VoteAdminInfo(vote.getId(), vote.getAdminKey()), choice.getId(),
        NEW_CHOICE_NAME
    );

    assertEquals(NEW_CHOICE_NAME, choice.getName());
  }

  @TestedBehaviour(VALIDATES_INPUTS)
  @Test
  public void when_ballots_are_already_issued_choices_cannot_be_modified() {
    vote.getBallots().add("Test Ballot");

    assertModifyChoice(
        vote.getId(), vote.getAdminKey(), choice.getId(), "something else"
    ).assertMessageIs("Vote modification disallowed: ballots already issued");
  }

  @TestedBehaviour(
    "if 'user' is used as adminKey, then the user must be the one who added the choice and canAddIn be true"
  )
  @Test
  public void userAdmin_cannot_modify_choice_if_canAddin_is_false() {
    vote.getParameters().setAddinable(false);

    assertModifyChoice(
        vote.getId(), USER, choice.getId(), NEW_CHOICE_NAME
    ).assertMessageIs(
        "Choice modification disallowed: adminKey is user, but canAddin is false"
    );

  }

  @TestedBehaviour(
    "if 'user' is used as adminKey, then the user must be the one who added the choice and canAddIn be true"
  )
  @Test
  public void
      userAdmin_cannot_modify_choice_if_it_is_not_added_by_other_user() {
    vote.getParameters().setAddinable(true);

    assertModifyChoice(vote.getId(), USER, choice.getId(), NEW_CHOICE_NAME)
        .assertMessageIs(
            "Choice modification disallowed: adminKey is user, " +
                "and the choice was added by a different user"
        );

  }

  @TestedBehaviour(
    "if 'user' is used as adminKey, then the user must be the one who added the choice and canAddIn be true"
  )
  @Test
  public void
      userAdmin_can_modify_the_choice_if_canAddin_is_true_and_he_is_the_choice_creator() {
    vote.getParameters().setAddinable(true);
    final Choice choice2 = new Choice("choice2", ADMIN);
    vote.addChoice(choice2);
    when(authService.getAuthenticatedUserName()).thenReturn(ADMIN);
    choiceService
        .modifyChoice(new VoteAdminInfo(vote.getId(), USER), choice2.getId(),
            NEW_CHOICE_NAME
        );

    assertEquals(NEW_CHOICE_NAME, choice2.getName());
  }

  private ThrowableTester assertModifyChoice(
      final String voteId, final String adminKey, final String choiceId,
      final String newName
  ) {
    return assertThrows(
        () -> choiceService.modifyChoice(
            new VoteAdminInfo(
                vote.getId(),
                USER
            ), choice.getId(), NEW_CHOICE_NAME
        )
    );
  }

}
