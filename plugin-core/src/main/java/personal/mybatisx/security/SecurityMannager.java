package personal.mybatisx.security;

import personal.mybatisx.security.impl.DefaultSecuritySupport;

public class SecurityMannager {
    
    
    
    private SecuritySupport securitySupport = new DefaultSecuritySupport();

    public void setSecuritySupport(SecuritySupport securitySupport) {
        this.securitySupport = securitySupport;
    }
    
    public  SecuritySupport getSecuritySupport(){
        return securitySupport;
    }

}
