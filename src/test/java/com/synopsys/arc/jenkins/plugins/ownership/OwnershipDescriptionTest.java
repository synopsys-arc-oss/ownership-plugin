package com.synopsys.arc.jenkins.plugins.ownership;

import hudson.model.User;
import hudson.security.HudsonPrivateSecurityRealm;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import jenkins.model.IdStrategy;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class OwnershipDescriptionTest {
    private static final IdStrategy CASE_SENSITIVE = new IdStrategy.CaseSensitive();

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Before
    public void setUp() throws Exception {
        applyIdStrategy(CASE_SENSITIVE);
    }

    private void applyIdStrategy(final IdStrategy idStrategy) throws IOException {
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null) {
            @Override
            public IdStrategy getUserIdStrategy() {
                return idStrategy;
            }

            @Override
            public IdStrategy getGroupIdStrategy() {
                return idStrategy;
            }
        };
        realm.createAccount("owner", "owner");
        j.jenkins.setSecurityRealm(realm);
    }

    @Test
    public void isOwnerShouldRespectCaseSensitiveIdStrategy() throws Exception {
        User user = User.get("owner");

        OwnershipDescription description = new OwnershipDescription(true, "owner", Collections.<String>emptyList());
        assertThat("OwnershipDescription doesn't respect case sensitive strategy", description.isOwner(user, false), equalTo(true));

        description = new OwnershipDescription(true, "OWNER", Collections.<String>emptyList());
        assertThat("OwnershipDescription doesn't respect case sensitive strategy", description.isOwner(user, false), equalTo(false));

        description = new OwnershipDescription(true, "another.owner", Arrays.asList("owner"));
        assertThat("OwnershipDescription doesn't respect case sensitive strategy", description.isOwner(user, true), equalTo(true));

        description = new OwnershipDescription(true, "ANOTHER.OWNER", Arrays.asList("OWNER"));
        assertThat("OwnershipDescription doesn't respect case sensitive strategy", description.isOwner(user, true), equalTo(false));
    }

    @Test
    public void isOwnerShouldRespectCaseInsensitiveIdStrategy() throws Exception {
        applyIdStrategy(IdStrategy.CASE_INSENSITIVE);
        User user = User.get("owner");

        OwnershipDescription description = new OwnershipDescription(true, "owner", Collections.<String>emptyList());
        assertThat("OwnershipDescription doesn't respect case sensitive strategy", description.isOwner(user, false), equalTo(true));

        description = new OwnershipDescription(true, "OWNER", Collections.<String>emptyList());
        assertThat("OwnershipDescription doesn't respect case sensitive strategy", description.isOwner(user, false), equalTo(true));

        description = new OwnershipDescription(true, "another.owner", Arrays.asList("owner"));
        assertThat("OwnershipDescription doesn't respect case sensitive strategy", description.isOwner(user, true), equalTo(true));

        description = new OwnershipDescription(true, "ANOTHER.OWNER", Arrays.asList("OWNER"));
        assertThat("OwnershipDescription doesn't respect case sensitive strategy", description.isOwner(user, true), equalTo(true));
    }


}