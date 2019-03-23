package org.rulez.demokracia.pdengine;

import org.junit.Test;
import org.rulez.demokracia.pdengine.annotations.TestedBehaviour;
import org.rulez.demokracia.pdengine.annotations.TestedFeature;
import org.rulez.demokracia.pdengine.annotations.TestedOperation;
import org.rulez.demokracia.pdengine.dataobjects.VoteAdminInfo;
import org.rulez.demokracia.pdengine.testhelpers.CreatedDefaultVoteRegistry;

@TestedFeature("Manage votes")
@TestedOperation("delete choice")
public class ChoiceDeleteAdminKeyIsUser extends CreatedDefaultVoteRegistry {

	private static final String USER = "user";
	private static final String CHOICE1 = "choice1";

	@TestedBehaviour("if \"user\" is used as adminKey, then the user must be the one who added the choice and canAddIn be true")
	@Test
	public void if_canAddin_is_false_then_other_users_cannot_add_choices() {
		Vote vote = voteManager.getVote(adminInfo.voteId);
		vote.canAddin = false;
		String choiceId = voteManager.addChoice(new VoteAdminInfo(adminInfo.voteId, adminInfo.adminKey), CHOICE1, TEST_USER_NAME);
		assertThrows(
			() -> voteManager.deleteChoice(new VoteAdminInfo(adminInfo.voteId, USER), choiceId)
		).assertMessageIs("The adminKey is \"user\" but canAddin is false.");
	}
	
	@TestedBehaviour("if \"user\" is used as adminKey, then the user must be the one who added the choice and canAddIn be true")
	@Test
	public void if_adminKey_is_user_and_the_user_is_not_the_one_who_added_the_choice_then_the_choice_cannot_be_deleted() {
		Vote vote = voteManager.getVote(adminInfo.voteId);
		vote.canAddin = true;
		String choiceId = voteManager.addChoice(new VoteAdminInfo(adminInfo.voteId, adminInfo.adminKey), CHOICE1, USER);
		assertThrows(
			() -> voteManager.deleteChoice(new VoteAdminInfo(adminInfo.voteId, USER), choiceId)
		).assertMessageIs("The adminKey is \"user\" but the user is not same with that user who added the choice.");
	}
	
	
	@TestedBehaviour("if \"user\" is used as adminKey, then the user must be the one who added the choice and canAddIn be true")
	@Test
	public void if_adminKey_is_user_and_canAddin_is_true_then_the_user_who_added_the_choice_is_able_to_delete_it() {
		String voteId = adminInfo.voteId;
		Vote vote = voteManager.getVote(voteId);
		vote.canAddin = true;
		String choiceId = voteManager.addChoice(new VoteAdminInfo(adminInfo.voteId, USER), CHOICE1, TEST_USER_NAME);
		voteManager.deleteChoice(new VoteAdminInfo(voteId, USER), choiceId);
		
		assertThrows(
			() -> voteManager.getChoice(voteId, choiceId)
		).assertMessageIs("Illegal choiceId");
	}
	
	@TestedBehaviour("if the vote has ballots issued, the choice cannot be deleted")
	@Test
	public void if_the_vote_has_issued_ballots_then_the_choice_cannot_be_deleted() {
		String voteId = adminInfo.voteId;
		String adminKey = adminInfo.adminKey;
		Vote vote = voteManager.getVote(voteId);
		String choiceId = voteManager.addChoice(new VoteAdminInfo(adminInfo.voteId, adminKey), CHOICE1, USER);
		vote.ballots.add("TestBallot");

		assertThrows(
			() -> voteManager.deleteChoice(new VoteAdminInfo(voteId, adminKey), choiceId)
		).assertMessageIs("Vote modification disallowed: ballots already issued");
	}
}