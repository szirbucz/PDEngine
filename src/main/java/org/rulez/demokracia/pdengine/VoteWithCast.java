package org.rulez.demokracia.pdengine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rulez.demokracia.pdengine.dataobjects.CastVote;

public class VoteWithCast {

	// We use member field instead of inheritance
	private Vote vote;
	private List<CastVote> votesCast;

	public VoteWithCast(final Vote vote) {
		this.vote = vote;
		votesCast = new ArrayList<>();
	}

	// Getters and setters could be eliminated with Project Lombok without
	// violating encapsulation.
	public Vote getVote() {
		return vote;
	}

	public void setVote(final Vote vote) {
		this.vote = vote;
	}

	public List<CastVote> getVotesCast() {
		return votesCast;
	}

	public void setVotesCast(final List<CastVote> votesCast) {
		this.votesCast = votesCast;
	}

	// Just copy-pasted this with minor modification. It's just an example.
	protected void addCastVote(final String proxyId, final List<RankedChoice> theVote) {
		Iterator<CastVote> listIterator = votesCast.iterator();
		while (listIterator.hasNext()) {
			CastVote element = listIterator.next();

			if (element.proxyId != null && element.proxyId.equals(proxyId)) {
				listIterator.remove();
			}
		}

		CastVote castVote = new CastVote(proxyId, theVote);
		votesCast.add(castVote);
	}

}
