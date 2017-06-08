/*
 * Marriage Registration APIs
 * APIs for Marriage registration for citizens are listed here.
 *
 * OpenAPI spec version: 1.0.0
 * Contact: swaminathan.s@riflexions.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package org.egov.mr.model;

import org.egov.mr.web.contract.ResponseInfo;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class ReportResponse {
	private ResponseInfo responseInfo;

	private ReportQuery reportQuery = null;
}