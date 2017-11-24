package vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import pojos.enums.BillStatusType;


@Data
@NoArgsConstructor
public class BillVo {
    private BillStatusType billStatusType;
    private Long billId;
    private String submit;
}
