/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * @author allad
 *
 */
@SuppressWarnings("deprecation")
public class MySQL5CustomInnoDBDialect extends MySQL5InnoDBDialect {
	/**
     * Call to super returns " type=innodb". 'Type' is a 
     * deprecated MySQL synonym for 'engine'         
     * so you may want to ignore the super call and use:
     * " engine=innodb ROW_FORMAT=DYNAMIC"       
     */
    public String getTableTypeString() {
        return super.getTableTypeString() + " ROW_FORMAT=DYNAMIC";
    }
}
