package DACN.DACN.services;

import DACN.DACN.entity.*;
import DACN.DACN.repository.OrderDetailRepository;
import DACN.DACN.repository.OrderRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Order createOrder(Order order) {
        // Lưu đối tượng Order vào cơ sở dữ liệu
        Order savedOrder = orderRepository.save(order); // Lưu đơn hàng
        // Lưu các chi tiết đơn hàng
        for (OrderDetail detail : savedOrder.getOrderDetails()) {
            detail.setOrder(savedOrder); // Thiết lập order cho chi tiết đơn hàng
            orderDetailRepository.save(detail); // Lưu chi tiết đơn hàng
        }

        return savedOrder; // Trả về đơn hàng đã lưu
    }
    /*public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }*/

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByIdDesc();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found with id: " + orderId));
    }
    // Phương thức lấy đơn hàng theo người dùng
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByIdDesc(user);
    }

    public void updateOrderStatus(Long orderId, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        // Tìm đơn hàng theo ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại")); // Ném lỗi nếu không tìm thấy

        // Cập nhật trạng thái cho đơn hàng nếu trạng thái mới khác với trạng thái hiện tại
        if (order.getStatus() != orderStatus) {
            order.setStatus(orderStatus);

            // Cập nhật ngày giao hàng thực tế nếu trạng thái là DELIVERED
            if (orderStatus == OrderStatus.DELIVERED) {
                order.updateActualDeliveryDate();
            }
        }

        // Cập nhật trạng thái thanh toán nếu trạng thái mới khác với trạng thái hiện tại
        if (order.getPaymentStatus() != paymentStatus) {
            order.setPaymentStatus(paymentStatus);
            // Nếu thanh toán không thành công, cập nhật trạng thái đơn hàng thành FAILED
            if (paymentStatus == PaymentStatus.FAILED) {
                order.setStatus(OrderStatus.CANCELED);
            }
        }

        // Lưu đơn hàng đã cập nhật
        orderRepository.save(order);
    }
    public List<Order> findOrders(String transactionCode, String customerName, String phone, OrderStatus status, Date startDate, Date endDate) {
        return orderRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            /*if (orderId != null && !orderId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), Long.valueOf(orderId)));
            }*/
            if (transactionCode != null && !transactionCode.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("transactionCode"), "%" + transactionCode + "%"));
            }
            if (customerName != null && !customerName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("customerName"), "%" + customerName + "%"));
            }

            if (phone != null && !phone.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + phone + "%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Kiểm tra và thêm tiêu chí tìm kiếm theo ngày đặt hàng
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("orderDate"), startDate, endDate));
            } else if (startDate != null) { // Chỉ có startDate
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            } else if (endDate != null) { // Chỉ có endDate
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }



    public List<Order> searchOrders(User user, String transactionCode, String status, Date startDate, Date endDate) {
        return orderRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Thêm tiêu chí người dùng để chỉ lấy đơn hàng của người dùng đó
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            //Tìm kiếm theo mã đơn hàng
            if (transactionCode != null && !transactionCode.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("transactionCode"), "%" + transactionCode + "%"));
            }
            // Kiểm tra và thêm tiêu chí tìm kiếm theo trạng thái đơn hàng
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), OrderStatus.valueOf(status)));
            }

            // Kiểm tra và thêm tiêu chí tìm kiếm theo ngày đặt hàng
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("orderDate"), startDate, endDate));
            } else if (startDate != null) { // Chỉ có startDate
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            } else if (endDate != null) { // Chỉ có endDate
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public void save(Order order) {

        orderRepository.save(order); // Lưu đơn hàng vào cơ sở dữ liệu
    }



    public Order findByTransactionCode(String transactionCode) {
        return orderRepository.findByTransactionCode(transactionCode);
    }
    public double getTodayRevenue() {
        // Lấy dữ liệu doanh thu cho ngày hôm nay và xử lý null
        Double revenue = orderRepository.calculateRevenueByDate(new Date());
        return revenue != null ? revenue : 0.0;
    }

    public double getThisWeekRevenue() {
        // Lấy dữ liệu doanh thu cho tuần này và xử lý null
        Double revenue = orderRepository.calculateRevenueByWeek();
        return revenue != null ? revenue : 0.0;
    }

    public double getThisMonthRevenue() {
        // Lấy dữ liệu doanh thu cho tháng này và xử lý null
        Double revenue = orderRepository.calculateRevenueByMonth();
        return revenue != null ? revenue : 0.0;
    }

    public double getThisYearRevenue() {
        // Lấy dữ liệu doanh thu cho năm này và xử lý null
        Double revenue = orderRepository.calculateRevenueByYear();
        return revenue != null ? revenue : 0.0;
    }

    private Double calculateRevenue(Date startDate, Date endDate) {
        // Truy vấn trực tiếp để tính tổng doanh thu
        return orderRepository.sumTotalAmountBetween(startDate, endDate);
    }

    public Double[] getMonthlyRevenue(int year) {
        Double[] monthlyRevenue = new Double[12];
        Arrays.fill(monthlyRevenue, 0.0); // Khởi tạo tất cả các tháng với 0.0

        for (int month = 1; month <= 12; month++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, 1); // Ngày đầu tháng
            Date startDate = calendar.getTime();

            // Đặt ngày cuối tháng
            calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = calendar.getTime();

            // Tính doanh thu cho tháng
            Double revenue = calculateRevenue(startDate, endDate);
            monthlyRevenue[month - 1] = revenue; // Gán doanh thu cho tháng
        }

        return monthlyRevenue;
    }

    public Double[] getDailyRevenue(int year, int month) {
        Double[] dailyRevenue = new Double[31]; // Tối đa 31 ngày
        Arrays.fill(dailyRevenue, 0.0); // Khởi tạo tất cả các ngày với 0.0

        // Tính số ngày trong tháng
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC")); // Đặt múi giờ nếu cần thiết
        calendar.set(year, month - 1, 1); // Đặt tháng và năm
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(year, month - 1, day); // Đặt ngày
            Date startDate = calendar.getTime();


            // Đặt endDate là cuối ngày
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date endDate = calendar.getTime();

            // Tính doanh thu cho ngày
            Double revenue = calculateRevenue(startDate, endDate);
            dailyRevenue[day - 1] = revenue; // Gán doanh thu cho ngày

            // Reset lại giờ về 0 cho ngày tiếp theo
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

        }

        return dailyRevenue;
    }
}