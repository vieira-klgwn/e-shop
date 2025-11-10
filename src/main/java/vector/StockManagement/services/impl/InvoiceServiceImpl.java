package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Invoice;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.InvoiceDisplayDTO;
import vector.StockManagement.repositories.InvoiceRepository;
import vector.StockManagement.services.InvoiceService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderServiceImpl orderServiceImpl;

    @Override
    public List<InvoiceDisplayDTO> findOverdueInvoices(){
        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceDisplayDTO> overdueInvoices = new ArrayList<>();
        for (Invoice invoice : invoices) {
            if(invoice.getDueDate().isBefore(LocalDate.now().plusDays(1)) ){
                InvoiceDisplayDTO invoiceDisplayDTO = getInvoiceDisplayDTO(invoice);
                overdueInvoices.add(invoiceDisplayDTO);
            }


        }


        return overdueInvoices;
    }

    @Override
    public List<InvoiceDisplayDTO> findAll(User userCreateBy) {

        return getInvoiceDisplayDTOS(userCreateBy);
    }

    private List<InvoiceDisplayDTO> getInvoiceDisplayDTOS(User userCreateBy) {
        List<Invoice> invoices = invoiceRepository.findAllByOrder_CreatedBy(userCreateBy);
        List<InvoiceDisplayDTO> dtos = new ArrayList<>();
        for (Invoice invoice : invoices) {
            InvoiceDisplayDTO dto = getInvoiceDisplayDTO(invoice);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<InvoiceDisplayDTO> findAll() {
        List <Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceDisplayDTO> dtos = new ArrayList<>();
        for(Invoice invoice : invoices) {
            InvoiceDisplayDTO dto = getInvoiceDisplayDTO(invoice);
            dtos.add(dto);
        }
        return dtos;
    }


    @Override
    public List<InvoiceDisplayDTO> getAll(User userCreateBy) {

        return getInvoiceDisplayDTOS(userCreateBy);
    }



    private InvoiceDisplayDTO getInvoiceDisplayDTO(Invoice invoice) {
        InvoiceDisplayDTO invoiceDisplayDTO = new InvoiceDisplayDTO();
        invoiceDisplayDTO.setInvoiceId(invoice.getId());
        invoiceDisplayDTO.setInvoiceAmount(invoice.getInvoiceAmount());
        invoiceDisplayDTO.setInvoiceNumber(invoice.getNumber());
        invoiceDisplayDTO.setCurrency(invoice.getCurrency());
        invoiceDisplayDTO.setStatus(invoice.getStatus().toString());
        invoiceDisplayDTO.setDueDate(invoice.getDueDate());
        invoiceDisplayDTO.setIssuedBy(invoice.getIssuedBy());
        invoiceDisplayDTO.setOrder(orderServiceImpl.getOrderDisplayDTO(invoice.getOrder()));
        invoiceDisplayDTO.setOrderBy(invoice.getOrder().getCreatedBy());
        return invoiceDisplayDTO;
    }

    @Override
    public InvoiceDisplayDTO findById(Long id) {

        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        assert invoice != null;
        return getInvoiceDisplayDTO(invoice);
    }

    @Override
    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new IllegalStateException("Invoice not found"));
    }

    @Override
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice update(Long invoiceId) {

        Invoice invoice= invoiceRepository.findById(invoiceId).orElse(null);
        assert invoice!= null;
        invoice.setInvoiceAmount(invoice.getInvoiceAmount());
        //put other updates

        return invoiceRepository.save(invoice);
    }

    @Override
    public void delete(Long id) {
        invoiceRepository.deleteById(id);
    }
}